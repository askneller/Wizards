//package com.example.examplemod;
//
//import net.minecraft.nbt.CompoundTag;
//import net.minecraftforge.common.capabilities.AutoRegisterCapability;
//
//@AutoRegisterCapability
//public class PlayerThirst {
//    private int thirst = 0;
////    private ManaTotemBlockEntity source; // todo will become an interface
//    private final int MAX_THIRST = 10;
//    private final int MIN_THIRST = 0;
//
//    public PlayerThirst(int thirst) {
//        this.thirst = thirst;
//    }
//
//    public PlayerThirst() {
//    }
//
//    public int getThirst() {
//        return thirst;
//    }
//
//    public void addThirst(int add) {
//        this.thirst = Math.min(thirst + add, MAX_THIRST);
//    }
//
//    public void subThirst(int sub) {
//        this.thirst = Math.max(thirst - sub, MIN_THIRST);
//    }
//
//    public void copyFrom(PlayerThirst source) {
//        this.thirst = source.thirst;
//    }
//
//    public void saveNBTDate(CompoundTag nbt) {
//        nbt.putInt("thirst", thirst);
//    }
//
//    public void loadNBTData(CompoundTag nbt) {
//        thirst = nbt.getInt("thirst");
//    }
//
//    @Override
//    public String toString() {
//        return "PlayerThirst{" +
//                "thirst=" + thirst +
//                '}';
//    }
//}
