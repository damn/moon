(ns ctx.add-stage-actors
  (:require [clojure.gdx.stage.add-actor :as add-actor]))

(defn f!
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [[f & params] actor-fns]
    (add-actor/f stage (apply f ctx params))))
