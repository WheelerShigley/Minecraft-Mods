package me.wheelershigley.www.solace_jobs.data.employments;

import com.google.common.reflect.TypeToken;
import me.wheelershigley.www.solace_jobs.data.Codec;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class EmploymentsCodec implements Codec<Employments, HashMap< UUID, Set<Identifier> > > {
    public static final EmploymentsCodec INSTANCE = new EmploymentsCodec();
    private static final Employments DATA = INSTANCE.load();

    @Override
    public Employments getDefaultCodec() {
        return getEmptyData();
    }

    @Override
    public Employments getEmptyData() {
        return new Employments();
    }

    @Override
    public Employments getInstanceData() {
        return DATA;
    }

    @Override
    public Type getDataType() {
        return new TypeToken< HashMap< UUID, Set<Identifier> > >() {}.getType();
    }

    @Override
    public String getConfigName() {
        return "solace_jobs_employments.json";
    }
}
