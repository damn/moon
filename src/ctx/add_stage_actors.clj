(ns ctx.add-stage-actors
  (:require [com.badlogic.gdx.scenes.scene2d.stage :as stage]))

(defn f!
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [[f & params] actor-fns]
    (stage/add-actor! stage (apply f ctx params))))
