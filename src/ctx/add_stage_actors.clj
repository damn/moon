(ns ctx.add-stage-actors
  (:require [scene2d.stage.add-actor :refer [add-actor!]]))

(defn f!
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [[f & params] actor-fns]
    (add-actor! stage (apply f ctx params))))
