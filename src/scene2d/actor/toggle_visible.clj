(ns scene2d.actor.toggle-visible
  (:require [scene2d.actor.set-visible :refer [set-visible!]]
            [scene2d.actor.visible :refer [visible?]]))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))
