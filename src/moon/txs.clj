(ns moon.txs)

(defprotocol Txs
  (handle! [_ txs]))
