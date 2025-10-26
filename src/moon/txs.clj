(ns moon.txs)

(defprotocol TransactionHandler
  (handle! [_ txs]))
