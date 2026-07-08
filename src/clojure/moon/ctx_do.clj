(ns clojure.moon.ctx-do
  (:require [clojure.txs-fn-map.actions :refer [actions!]]
            [clojure.moon.tx :as tx]))

(defn do!
  [ctx txs]
  (try (actions! tx/f ctx txs)
       (catch Throwable t
         (throw (ex-info "Error handling txs"
                         {:txs txs} t)))))
