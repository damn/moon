(ns moon.create.add-stage-actors
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn step
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [actor (map (fn [[f & params]] (apply f ctx params)) actor-fns)]
    (Stage/.addActor stage actor))
  ctx)

; TODO test?
; ctx.stage?
