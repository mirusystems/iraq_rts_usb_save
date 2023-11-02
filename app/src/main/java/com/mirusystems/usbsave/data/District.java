package com.mirusystems.usbsave.data;

public class District {
    public Integer code;
    public String name;
    public Integer totalCount;
    public Integer completedCount;

//    public int getCode() {
//        if (code != null) {
//            try {
//                return Integer.parseInt(code);
//            } catch (NumberFormatException ignore) {
//            }
//        }
//        return -1;
//    }


    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getTotalCount() {
        if (totalCount == null) {
            return 0;
        }
        return totalCount;
    }

    public int getCompletedCount() {
        if (completedCount == null) {
            return 0;
        }
        return completedCount;
    }

    @Override
    public String toString() {
        return "District{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", totalCount=" + totalCount +
                ", completedCount=" + completedCount +
                '}';
    }
}
