(ns game.ctx.do
  (:require [clojure.core.actions :refer [actions!]]
            [clojure.core.reduce-actions :refer [reduce-actions!]]))

(defn do!
  [{:keys [ctx/txs-fn-map
           ctx/reaction-txs-fn-map]
    :as ctx}
   txs]
  (let [handled-txs (try (actions! txs-fn-map ctx txs)
                         (catch Throwable t
                           (throw (ex-info "Error handling txs"
                                           {:txs txs} t))))]
    (reduce-actions! reaction-txs-fn-map
                     ctx
                     handled-txs)))
