(ns com.badlogic.gdx.scenes.scene2d.actor.toggle-visible
  (:require [com.badlogic.gdx.scenes.scene2d.actor.set-visible :refer [set-visible!]]
            [com.badlogic.gdx.scenes.scene2d.actor.visible :refer [visible?]]))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))
