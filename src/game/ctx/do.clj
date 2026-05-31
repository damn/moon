(ns game.ctx.do
  (:require [clojure.core-ext :refer [actions!
                                      reduce-actions!]]
            [game.constants :refer [txs-fn-map]]
            [game.reaction-txs :as reaction-txs]))

(defn do! [ctx txs]
  (let [handled-txs (try (actions! txs-fn-map ctx txs)
                         (catch Throwable t
                           (throw (ex-info "Error handling txs"
                                           {:txs txs} t))))]
    (reduce-actions! reaction-txs/fn-map
                     ctx
                     handled-txs)))
