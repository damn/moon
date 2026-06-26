(ns scene2d.actor.stage-local-coordinates
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn stage->local-coordinates [actor vector2]
  (actor/stage->local-coordinates actor vector2))
