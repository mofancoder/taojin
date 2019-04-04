package com.tj.util.unique;


import com.tj.util.unique.service.IMachineFactory;

/**
 * 创建时间：2017/3/1
 */
public class Unique {

    private static final long DEFAULT_WORKER_BITS = 10L;
    private static final long DEFAULT_SEQUENCE_BITS = 12L;
    private IdWorker idWorker;
    //最大机器码位数
    private long maxWorkerBits = DEFAULT_WORKER_BITS;
    //最大序号位数
    private long maxSequenceBits = DEFAULT_SEQUENCE_BITS;

    /**
     * 唯一序号生成
     *
     * @param factory 机器码生成工厂
     */
    public Unique(IMachineFactory factory) {
        if (factory == null) {
            throw new RuntimeException("factory is null");
        }
        idWorker = new IdWorker(factory.machineId(-1L ^ -1L << maxWorkerBits), maxWorkerBits, maxSequenceBits);
    }

    /**
     * 唯一序号生成
     *
     * @param factory         机器码生成工厂
     * @param maxWorkerBits   最大机器码位数
     * @param maxSequenceBits 每毫秒最大位数
     */
    public Unique(IMachineFactory factory, long maxWorkerBits, long maxSequenceBits) {
        if (factory == null) {
            throw new RuntimeException("factory is null");
        }
        idWorker = new IdWorker(factory.machineId(-1L ^ -1L << maxWorkerBits), maxWorkerBits, maxSequenceBits);
    }

    /**
     * 获取一个序号
     *
     * @return
     */
    public Long nextId() {
        return idWorker.nextId();
    }

    //序号生成器
    private class IdWorker {

        //开始时间戳
        private final static long twepoch = 1361753741828L;
        //机器码
        private final long workerId;
        //最大机器码
        private final long maxWorkerId;
        //机器码位移数
        private final long workerIdShift;
        //时间戳位移数
        private final long timestampLeftShift;
        //最大序号
        private final long sequenceMask;
        //机器码位数
        private long workerIdBits;
        //序号位数
        private long sequenceBits;
        //序号
        private long sequence = 0L;
        private long lastTimestamp = -1L;

        public IdWorker(final long workerId) {
            super();
            workerIdBits = DEFAULT_WORKER_BITS;
            sequenceBits = DEFAULT_SEQUENCE_BITS;
            //最大机器码
            maxWorkerId = -1L ^ -1L << workerIdBits;
            //时间戳位移数
            timestampLeftShift = sequenceBits + workerIdBits;
            //最大序号
            sequenceMask = -1L ^ -1L << sequenceBits;
            //机器码位移数
            workerIdShift = sequenceBits;
            if (workerId > maxWorkerId || workerId < 0) {
                throw new IllegalArgumentException(String.format(
                        "worker Id can't be greater than %d or less than 0", maxWorkerId));
            }
            this.workerId = workerId;
        }

        public IdWorker(final long workerId, long workerIdBits, long sequenceBits) {
            super();
            //最大机器码
            maxWorkerId = -1L ^ -1L << workerIdBits;
            //时间戳位移数
            timestampLeftShift = sequenceBits + workerIdBits;
            //最大序号
            sequenceMask = -1L ^ -1L << sequenceBits;
            //机器码位移数
            workerIdShift = sequenceBits;
            if (workerId > maxWorkerId || workerId < 0) {
                throw new IllegalArgumentException(String.format(
                        "worker Id can't be greater than %d or less than 0", maxWorkerId));
            }
            this.workerId = workerId;
        }

        public synchronized long nextId() {
            long timestamp = this.timeGen();
            if (this.lastTimestamp == timestamp) {
                this.sequence = (this.sequence + 1) & sequenceMask;
                if (this.sequence == 0) {
                    timestamp = this.tilNextMillis(this.lastTimestamp);
                }
            } else {
                this.sequence = 0;
            }
            if (timestamp < this.lastTimestamp) {
                try {
                    throw new Exception(String.format(
                            "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                            this.lastTimestamp - timestamp));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            this.lastTimestamp = timestamp;
            long nextId = ((timestamp - twepoch << timestampLeftShift))
                    | (this.workerId << workerIdShift) | (this.sequence);
            return nextId;
        }

        private long tilNextMillis(final long lastTimestamp) {
            long timestamp = this.timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = this.timeGen();
            }
            return timestamp;
        }

        private long timeGen() {
            return System.currentTimeMillis();
        }


    }

}
