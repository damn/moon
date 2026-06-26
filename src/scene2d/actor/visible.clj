(ns scene2d.actor.visible
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn visible? [actor]
  (actor/visible? actor))
