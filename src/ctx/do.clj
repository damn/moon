(ns ctx.do
  (:require [clojure.actions :refer [actions!]]))

(defn do!
  [{:keys [ctx/txs-fn-map]
    :as ctx}
   txs]
  (try (actions! txs-fn-map ctx txs)
       (catch Throwable t
         (throw (ex-info "Error handling txs"
                         {:txs txs} t)))))
