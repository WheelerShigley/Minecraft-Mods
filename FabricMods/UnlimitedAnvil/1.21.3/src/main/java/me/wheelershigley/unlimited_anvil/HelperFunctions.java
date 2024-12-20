package me.wheelershigley.unlimited_anvil;

import me.wheelershigley.unlimited_anvil.item_categories.ItemCategories;
import me.wheelershigley.unlimited_anvil.item_categories.ItemCategory;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.wheelershigley.unlimited_anvil.UnlimitedAnvil.Conflicts;
import static me.wheelershigley.unlimited_anvil.UnlimitedAnvil.MaximumEnchantLevels;

public class HelperFunctions {
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }

    public static <T> boolean contains(T[] set, T element) {
        if(set == null) { return false; }

        for(T subset : set) {
            if(subset.equals(element) ) {
                return true;
            }
        }
        return false;
    }

    public static ItemEnchantmentsComponent combineEnchants(ItemStack PrimaryItemStack, ItemStack SecondaryItemStack) {
        ItemEnchantmentsComponent PrimaryEnchantments = EnchantmentHelper.getEnchantments(PrimaryItemStack);
        ItemEnchantmentsComponent SecondaryEnchantments = EnchantmentHelper.getEnchantments(SecondaryItemStack);

        ArrayList< RegistryKey<Enchantment> > ValidEnchants = getValidEnchants(PrimaryItemStack);

        ItemEnchantmentsComponent.Builder ResultBuilder = new ItemEnchantmentsComponent.Builder(PrimaryEnchantments);
        Set<RegistryEntry<Enchantment>> SecondaryEnchants = SecondaryEnchantments.getEnchantments();
        int previous_level, current_level;
        for(RegistryEntry<Enchantment> SecondaryEnchant : SecondaryEnchants) {
            /*Skip adding if it's invalid for this item*/
            if(
                PrimaryItemStack.getItem() != Items.ENCHANTED_BOOK
                && !ValidEnchants.contains( SecondaryEnchant.getKey().get() )
            ) {
                continue;
            }

            /*Skip adding the current enchant if it conflicts*/
            //TODO: change out with vanilla-like, overridden system
            if(  Conflicts.containsKey( SecondaryEnchant.getKey().get().getValue() )  ) {
                Identifier[] PrimaryIdentifiers = new Identifier[PrimaryEnchantments.getSize()];
                int index = 0;
                for(RegistryEntry<Enchantment> PrimaryEnchantment : PrimaryEnchantments.getEnchantments() ) {
                    PrimaryIdentifiers[index] = PrimaryEnchantment.getKey().get().getValue();
                    index++;
                }
                if(
                    identifiersIntersect(
                        PrimaryIdentifiers,
                        Conflicts.get( SecondaryEnchant.getKey().get().getValue() )
                    )
                ) {
                    continue;
                }
            }

            previous_level = PrimaryEnchantments.getLevel(SecondaryEnchant);
            current_level = SecondaryEnchantments.getLevel(SecondaryEnchant);

            /*Combine enchants if appropriate*/
            current_level = (previous_level == current_level) ? current_level+1 : Math.max(previous_level,current_level);
            /*Truncate enchant-levels by maximum-effective levels*/
            Identifier EnchantIdentifier = null;
            if( SecondaryEnchant.getKey().isPresent() ) {
                EnchantIdentifier = SecondaryEnchant.getKey().get().getValue();
            }
            if(EnchantIdentifier != null) {
                current_level = Math.min( current_level, MaximumEnchantLevels.get(EnchantIdentifier) );
            }

            ResultBuilder.set(SecondaryEnchant, current_level);
        }

        //return sortEnchants( ResultBuilder.build() );
        return ResultBuilder.build();
    }

    /*public static ItemEnchantmentsComponent sortEnchants(ItemEnchantmentsComponent ItemEnchantments) {
        ArrayList< RegistryEntry<Enchantment> > SortedEnchantIdentifiers = new ArrayList<>();

        *//*Add all to list, to be sorted*//*
        for(RegistryEntry<Enchantment> enchantEntry : ItemEnchantments.getEnchantments() ) {
            if( enchantEntry.getKey().isEmpty() ) { continue; }
            SortedEnchantIdentifiers.add(enchantEntry);
        }

        *//*Sort the list*//*
        SortedEnchantIdentifiers.sort(
            (first, second) -> {
                if( first.getKey().isEmpty() || second.getKey().isEmpty() ) { return 0; }

                String firstName = first.getKey().get().getValue().toTranslationKey();
                String secondName = second.getKey().get().getValue().toTranslationKey();
                return firstName.compareTo(secondName);
            }
        );

        ItemEnchantmentsComponent.Builder sortedEnchants = new ItemEnchantmentsComponent.Builder(ItemEnchantments);
        *//*Remove all entries*//*
        for(RegistryEntry<Enchantment> PossibleEnchant : sortedEnchants.getEnchantments() ) {
            //Setting the value <= 0 removes the entry
            sortedEnchants.set(PossibleEnchant, 0);
        }
        *//*Add them back, in order (based on the sorted list)*//*
        for(RegistryEntry<Enchantment> SortedEnchantIdentifier : SortedEnchantIdentifiers) {
            sortedEnchants.add(
                SortedEnchantIdentifier,
                ItemEnchantments.getLevel(SortedEnchantIdentifier )
            );
        }

        return sortedEnchants.build();
    }*/


    private static int getStoredEnchantmentLevel(RegistryEntry<Enchantment> enchantment, ItemStack stack, boolean useStoredEnchants) {
        ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(
            useStoredEnchants ? DataComponentTypes.STORED_ENCHANTMENTS : DataComponentTypes.ENCHANTMENTS,
            ItemEnchantmentsComponent.DEFAULT
        );
        return itemEnchantmentsComponent.getLevel(enchantment);
    }
    public static int getEnchantingCost(ItemStack enchantedItem, boolean useStoredEnchants) {
        int accumulator = 0;
        Set< RegistryEntry<Enchantment> > Enchantments = EnchantmentHelper.getEnchantments(enchantedItem).getEnchantments();
        for(RegistryEntry<Enchantment> enchant : Enchantments) {
            accumulator += enchant.value().getAnvilCost() * getStoredEnchantmentLevel(enchant, enchantedItem, useStoredEnchants);
        }
        return 2*accumulator;
    }

    public static HashMap<Identifier, Integer> getMaximumEffectiveEnchantLevels() {
        HashMap<Identifier, Integer> MaximumEffectiveEnchantLevels = new HashMap<>(); {
            MaximumEffectiveEnchantLevels.put(Enchantments.AQUA_AFFINITY        .getValue(), 1                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.BANE_OF_ARTHROPODS   .getValue(), Integer.MAX_VALUE  );
            MaximumEffectiveEnchantLevels.put(Enchantments.BLAST_PROTECTION     .getValue(), 10                 );
            MaximumEffectiveEnchantLevels.put(Enchantments.BREACH               .getValue(), Integer.MAX_VALUE  );
            MaximumEffectiveEnchantLevels.put(Enchantments.CHANNELING           .getValue(), 1                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.BINDING_CURSE        .getValue(), 1                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.VANISHING_CURSE      .getValue(), 1                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.DENSITY              .getValue(), Integer.MAX_VALUE  );
            MaximumEffectiveEnchantLevels.put(Enchantments.DEPTH_STRIDER        .getValue(), 3                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.EFFICIENCY           .getValue(), 255                );
            MaximumEffectiveEnchantLevels.put(Enchantments.FEATHER_FALLING      .getValue(), 7                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.FIRE_ASPECT          .getValue(), 255                );
            MaximumEffectiveEnchantLevels.put(Enchantments.FIRE_PROTECTION      .getValue(), 10                 );
            MaximumEffectiveEnchantLevels.put(Enchantments.FLAME                .getValue(), 1                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.FORTUNE              .getValue(), 255                );
            MaximumEffectiveEnchantLevels.put(Enchantments.FROST_WALKER         .getValue(), 14                 );
            MaximumEffectiveEnchantLevels.put(Enchantments.IMPALING             .getValue(), Integer.MAX_VALUE  );
            MaximumEffectiveEnchantLevels.put(Enchantments.INFINITY             .getValue(), 1                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.KNOCKBACK            .getValue(), 255                );
            MaximumEffectiveEnchantLevels.put(Enchantments.LOOTING              .getValue(), 255                );
            MaximumEffectiveEnchantLevels.put(Enchantments.LOYALTY              .getValue(), 127                );
            MaximumEffectiveEnchantLevels.put(Enchantments.LUCK_OF_THE_SEA      .getValue(), 255                );
            MaximumEffectiveEnchantLevels.put(Enchantments.LURE                 .getValue(), 5                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.MENDING              .getValue(), 1                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.MULTISHOT            .getValue(), 1                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.PIERCING             .getValue(), 127                );
            MaximumEffectiveEnchantLevels.put(Enchantments.POWER                .getValue(), 255                );
            MaximumEffectiveEnchantLevels.put(Enchantments.PROJECTILE_PROTECTION.getValue(), 10                 );
            MaximumEffectiveEnchantLevels.put(Enchantments.PROTECTION           .getValue(), 20                 );
            MaximumEffectiveEnchantLevels.put(Enchantments.PUNCH                .getValue(), 255                );
            MaximumEffectiveEnchantLevels.put(Enchantments.QUICK_CHARGE         .getValue(), 5                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.RESPIRATION          .getValue(), 255                );
            MaximumEffectiveEnchantLevels.put(Enchantments.RIPTIDE              .getValue(), 255                );
            MaximumEffectiveEnchantLevels.put(Enchantments.SHARPNESS            .getValue(), Integer.MAX_VALUE  );
            MaximumEffectiveEnchantLevels.put(Enchantments.SILK_TOUCH           .getValue(), 1                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.SMITE                .getValue(), Integer.MAX_VALUE  );
            MaximumEffectiveEnchantLevels.put(Enchantments.SOUL_SPEED           .getValue(), 255                );
            MaximumEffectiveEnchantLevels.put(Enchantments.SWEEPING_EDGE        .getValue(), 255                );
            MaximumEffectiveEnchantLevels.put(Enchantments.SWIFT_SNEAK          .getValue(), 5                  );
            MaximumEffectiveEnchantLevels.put(Enchantments.THORNS               .getValue(), Integer.MAX_VALUE  );
            MaximumEffectiveEnchantLevels.put(Enchantments.UNBREAKING           .getValue(), 255                );
            MaximumEffectiveEnchantLevels.put(Enchantments.WIND_BURST           .getValue(), 255                );
        }
        return MaximumEffectiveEnchantLevels;
    }

    private static ArrayList< Pair<Identifier, Identifier[]> > getConflictMappings(Identifier[] enchantments) {
        ArrayList< Pair<Identifier, Identifier[]> > ConflictMappings = new ArrayList<>();
        for(Identifier Enchant : enchantments) {
            ArrayList<Identifier> OtherEnchants = new ArrayList<>();
            for(int index = 0; index < enchantments.length; index++) {
                if( !enchantments[index].equals(Enchant) ) {
                    OtherEnchants.add( enchantments[index] );
                }
            }
            ConflictMappings.add(
                new Pair<>(
                    Enchant,
                    OtherEnchants.toArray(new Identifier[0])
                )
            );
        }
        return ConflictMappings;
    }


    public static ArrayList< RegistryKey<Enchantment> > getValidEnchants(ItemStack item) {
        ArrayList< RegistryKey<Enchantment> > ValidEnchants = new ArrayList<>();
        /*Special case, I should probably re-work how this works to avoid this*/
        if( item.getItem().equals(Items.CARVED_PUMPKIN) ) {
            ValidEnchants.add(Enchantments.BINDING_CURSE);
        }

        String ItemName = item.getItem().toString().split(":")[1];

        ArrayList< ItemCategory > categories = new ArrayList<>();
        Pattern RegexPattern = null;
        Matcher RegexMatcher = null;
        for(ItemCategory PotentialCategory : ItemCategories.ItemCategories) {
            RegexPattern = Pattern.compile("[^a-z]"+PotentialCategory.suffix+"$");
            RegexMatcher = RegexPattern.matcher(ItemName);
            if( RegexMatcher.find() || ItemName.equals(PotentialCategory.suffix) ) {
                categories.add(PotentialCategory);
            }
        }

        //If this isn't enchantable, return nothing
        if(categories.size() <= 0) { return ValidEnchants; }

        /*Always valid enchants*/
        ValidEnchants.add(Enchantments.VANISHING_CURSE);
        if( item.isDamageable() ) {
            ValidEnchants.add(Enchantments.UNBREAKING);
            ValidEnchants.add(Enchantments.MENDING);
        }

        /*Tools*/
        if(
            categories.contains(ItemCategories.PickaxeCategory  )
            || categories.contains(ItemCategories.AxeCategory   )
            || categories.contains(ItemCategories.ShovelCategory)
            || categories.contains(ItemCategories.HoeCategory   )
        ) {
            ValidEnchants.add(Enchantments.EFFICIENCY);
            ValidEnchants.add(Enchantments.SILK_TOUCH);
            ValidEnchants.add(Enchantments.FORTUNE);
        }

        /*Melee Weapons*/
        if(
            categories.contains(ItemCategories.SwordCategory)
            || categories.contains(ItemCategories.AxeCategory)
            || categories.contains(ItemCategories.TridentCategory)
        ) {
            ValidEnchants.add(Enchantments.SHARPNESS);
            ValidEnchants.add(Enchantments.SMITE);
            ValidEnchants.add(Enchantments.BANE_OF_ARTHROPODS);
            ValidEnchants.add(Enchantments.KNOCKBACK);
            ValidEnchants.add(Enchantments.FIRE_ASPECT);
        }
        if( categories.contains(ItemCategories.SwordCategory) ) {
            ValidEnchants.add(Enchantments.SWEEPING_EDGE);
        }

        /*Ranged Weapons*/
        if( categories.contains(ItemCategories.BowCategory) ) {
            ValidEnchants.add(Enchantments.POWER);
            ValidEnchants.add(Enchantments.PUNCH);
            ValidEnchants.add(Enchantments.FLAME);
            ValidEnchants.add(Enchantments.LOOTING);
        }
        if( categories.contains(ItemCategories.CrossbowCategory) ) {
            ValidEnchants.add(Enchantments.MULTISHOT);
            ValidEnchants.add(Enchantments.PIERCING);
        }
        if( categories.contains(ItemCategories.FishingRodCategory) ) {
            ValidEnchants.add(Enchantments.LUCK_OF_THE_SEA);
            ValidEnchants.add(Enchantments.LURE);
        }

        /*Armor*/
        if(
            categories.contains(ItemCategories.HelmetCategory)
            || categories.contains(ItemCategories.ChestplateCategory)
            || categories.contains(ItemCategories.LeggingsCategory)
            || categories.contains(ItemCategories.BootsCategory)
            || categories.contains(ItemCategories.HorseArmorCategory)
            || categories.contains(ItemCategories.WolfArmorCategory)
        ) {
            ValidEnchants.add(Enchantments.BLAST_PROTECTION);
            ValidEnchants.add(Enchantments.FIRE_PROTECTION);
            ValidEnchants.add(Enchantments.PROTECTION);
            ValidEnchants.add(Enchantments.PROJECTILE_PROTECTION);
            ValidEnchants.add(Enchantments.THORNS);
            ValidEnchants.add(Enchantments.VANISHING_CURSE);
        }
        if( categories.contains(ItemCategories.HelmetCategory) ) {
            ValidEnchants.add(Enchantments.RESPIRATION);
            ValidEnchants.add(Enchantments.AQUA_AFFINITY);
        }
        if( categories.contains(ItemCategories.LeggingsCategory) ) {
            ValidEnchants.add(Enchantments.SWIFT_SNEAK);
        }
        if( categories.contains(ItemCategories.BootsCategory) ) {
            ValidEnchants.add(Enchantments.DEPTH_STRIDER);
            ValidEnchants.add(Enchantments.SOUL_SPEED);
        }

        /*Unique Enchants*/
        if( categories.contains(ItemCategories.MaceCategory) ) {
            ValidEnchants.add(Enchantments.DENSITY);
            ValidEnchants.add(Enchantments.BREACH);
            ValidEnchants.add(Enchantments.WIND_BURST);
        }
        if( categories.contains(ItemCategories.TridentCategory) ) {
            ValidEnchants.add(Enchantments.LOYALTY);
            ValidEnchants.add(Enchantments.RIPTIDE);
            ValidEnchants.add(Enchantments.CHANNELING);
        }

        return ValidEnchants;
    }

    private static void addToMap(HashMap<Identifier, Identifier[]> Map, ArrayList< Pair<Identifier, Identifier[]> > NotYetAddeds) {
        /*Input Validation*/
        if( Map == null || NotYetAddeds == null || NotYetAddeds.isEmpty() ) {
            return;
        }

        for(Pair<Identifier, Identifier[]> NotYetAdded : NotYetAddeds) {
            if(  Map.containsKey( NotYetAdded.getLeft() )  ) {
                Identifier[] ConflictsForExistingEnchant = Map.get( NotYetAdded.getLeft() );
                ArrayUtils.add( ConflictsForExistingEnchant, NotYetAdded.getRight() );
                Map.replace(
                    NotYetAdded.getLeft(),
                    ConflictsForExistingEnchant
                );
            } else {
                Map.put(
                    NotYetAdded.getLeft(),
                    NotYetAdded.getRight()
                );
            }
        }
    }
    public static HashMap<Identifier, Identifier[]> getConflicts() {
        HashMap<Identifier, Identifier[]> Conflicts = new HashMap<>();

        ArrayList< Pair<Identifier, Identifier[]> > CurrentConflicts = null;
        /*Silk_Touch & Fortune*/
        CurrentConflicts = getConflictMappings(
            new Identifier[]{
                Enchantments.SILK_TOUCH.getValue(),
                Enchantments.FORTUNE.getValue()
            }
        );
        addToMap(Conflicts, CurrentConflicts);

        /*Protections*/
        CurrentConflicts = getConflictMappings(
            new Identifier[]{
                Enchantments.BLAST_PROTECTION.getValue(),
                Enchantments.FIRE_PROTECTION.getValue(),
                Enchantments.PROTECTION.getValue(),
                Enchantments.PROJECTILE_PROTECTION.getValue()
            }
        );
        addToMap(Conflicts, CurrentConflicts);

        /*Weapon Specializations*/
        CurrentConflicts = getConflictMappings(
            new Identifier[]{
                Enchantments.SHARPNESS.getValue(),
                Enchantments.SMITE.getValue(),
                Enchantments.BANE_OF_ARTHROPODS.getValue()
            }
        );
        addToMap(Conflicts, CurrentConflicts);

        return Conflicts;
    }

    public static boolean identifiersIntersect(Identifier[] LeftSet, Identifier[] RightSet) {
        for(Identifier LeftSubset : LeftSet) {
            for(Identifier RightSubset : RightSet) {
                if( LeftSubset.equals(RightSubset) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean enchantsAreTheSame(ItemStack FirstItem, ItemStack SecondItem) {
        ArrayList< RegistryEntry<Enchantment> > PrimaryEnchantsList = new ArrayList<>();
        ItemEnchantmentsComponent PrimaryEnchantments = EnchantmentHelper.getEnchantments(FirstItem);
            PrimaryEnchantsList.addAll( PrimaryEnchantments.getEnchantments() );
        ArrayList< RegistryEntry<Enchantment> > SecondaryEnchantsList = new ArrayList<>();
        ItemEnchantmentsComponent SecondaryEnchants = EnchantmentHelper.getEnchantments(SecondItem);
            SecondaryEnchantsList.addAll( SecondaryEnchants.getEnchantments() );

        //Trivial Case
        if( PrimaryEnchantsList.size() != SecondaryEnchantsList.size() ) { return false; }

        //Subtraction (SetB - SetA)
        for(RegistryEntry<Enchantment> PrimaryEnchantment : PrimaryEnchantsList) {
            if(
                SecondaryEnchantsList.contains(PrimaryEnchantment)
                && PrimaryEnchantments.getLevel(PrimaryEnchantment) == SecondaryEnchants.getLevel(PrimaryEnchantment)
            ) {
                SecondaryEnchantsList.remove(PrimaryEnchantment);
            } else {
                return false;
            }
        }

        return SecondaryEnchantsList.isEmpty();
    }
}
