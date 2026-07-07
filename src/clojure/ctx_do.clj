(ns clojure.ctx-do
  (:require [clojure.actions :refer [actions!]]
            [clojure.tx-fn-map :as tx-fn-map]))

(defn do!
  [ctx txs]
  (try (actions! tx-fn-map/f ctx txs)
       (catch Throwable t
         (throw (ex-info "Error handling txs"
                         {:txs txs} t)))))
