(ns moon.ctx)

(defprotocol TransactionHandler
  (handle! [_ txs]))
