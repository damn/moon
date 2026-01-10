(ns moon.txs) ; integarte

(defprotocol TransactionHandler
  (handle! [_ txs]))
