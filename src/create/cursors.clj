(ns create.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [game.ctx.create-cursor :refer [create-cursor]]))

(defn step
  [ctx]
  (assoc ctx :ctx/cursors (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
                            (update-vals data
                                         (fn [[path hotspot-position]]
                                           (create-cursor ctx
                                                          (format path-format path)
                                                          hotspot-position))))))
