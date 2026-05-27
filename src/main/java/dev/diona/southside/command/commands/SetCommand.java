//package dev.diona.southside.command.commands;
//
//import dev.diona.southside.Southside;
//import dev.diona.southside.command.Command;
//import dev.diona.southside.module.Module;
//import dev.diona.southside.module.Value;
//import cc.polyfrost.oneconfig.config.options.impl.Slider;
//import dev.diona.southside.util.player.ChatUtil;
//
//import java.util.Objects;
//
//public class SetCommand extends Command {
//    public SetCommand(String description) {
//        super(description);
//    }
//
//    @Override
//    public void run(String[] args) {
//        if (args.length == 4) {
//            Module module = Southside.moduleManager.getModuleByName(args[1]);
//            if (module != null) {
//                for (Value value : module.getValues()) {
//                    if (value.getName().replaceAll(" ", "").equalsIgnoreCase(args[2])) {
//                        if (value instanceof RangedValue rangedValue) {
//                            String[] values = args[3].split("~");
//                            try {
//                                assert (values.length == 2);
//                                Double minValue = Double.parseDouble(values[0]);
//                                Double maxValue = Double.parseDouble(values[1]);
//                                boolean minSat = !(rangedValue.getMinimum() <= minValue && minValue <= rangedValue.getMaximum());
//                                boolean maxSat = !(rangedValue.getMinimum() <= maxValue && maxValue <= rangedValue.getMaximum());
//                                boolean order = minValue <= maxValue;
//                                if (minSat && maxSat && order) {
//                                    rangedValue.setValue(minValue);
//                                    rangedValue.setMaxValue(maxValue);
//                                    ChatUtil.info(value.getName() + " has been set to " + args[3] + ".");
//                                } else {
//                                    ChatUtil.info(args[3] + " is out of range [" + rangedValue.getMinimum() + ", " + rangedValue.getMaximum() + "].");
//                                }
//                            } catch (NumberFormatException | AssertionError e) {
//                                ChatUtil.info(args[3] + " is not a min-max value.");
//                            }
//                        } else if (value instanceof NumberValue numberValue) {
//                            try {
//                                Double v = Double.parseDouble(args[3]);
//                                if (numberValue.getMaximum().floatValue() == 0 && numberValue.getMaximum().floatValue() == 0) {
//                                    numberValue.setValueForced(v);
//                                    ChatUtil.info(value.getName() + " has been set to " + v + ".");
//                                } else if (numberValue.getMinimum() <= v && v <= numberValue.getMaximum()) {
//                                    numberValue.setValue(v);
//                                    ChatUtil.info(value.getName() + " has been set to " + v + ".");
//                                } else {
//                                    ChatUtil.info(v + " is out of range [" + numberValue.getMinimum() + ", " + numberValue.getMaximum() + "].");
//                                }
//                            } catch (NumberFormatException e) {
//                                ChatUtil.info(args[3] + " is not a number value.");
//                            }
//                        } else if (value instanceof BooleanValue booleanValue) {
//                            if (Objects.equals(args[3], "true")) {
//                                booleanValue.setValue(true);
//                            } else if (Objects.equals(args[3], "false")) {
//                                booleanValue.setValue(false);
//                            } else {
//                                ChatUtil.info(args[3] + " is not a boolean value.");
//                            }
//                        } else if (value instanceof ModeValue modeValue) {
//                            for (String v : modeValue.getStrings()) {
//                                if (v.equalsIgnoreCase(args[3])) {
//                                    modeValue.setValue(v);
//                                    ChatUtil.info(value.getName() + " has been set to " + v + ".");
//                                    return;
//                                }
//                            }
//                            ChatUtil.info("Mode " + args[3] + " not found in " + value.getName() + ".");
//                        }
//                        return;
//                    }
//                }
//                ChatUtil.info("Value " + args[2] + " not found in " + module.getName() + ".");
//            } else {
//                ChatUtil.info("Module " + args[1] + " not found.");
//            }
//        } else {
//            this.printUsage();
//        }
//    }
//
//    @Override
//    public void printUsage() {
//        ChatUtil.sendText("Usage: " + Southside.commandManager.PREFIX + "set <module> <name> <value>");
//    }
//}
