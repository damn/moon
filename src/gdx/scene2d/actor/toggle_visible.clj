(ns gdx.scene2d.actor.toggle-visible
  (:require [gdx.scene2d.actor.set-visible :refer [set-visible!]]
            [gdx.scene2d.actor.visible :refer [visible?]]))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))
