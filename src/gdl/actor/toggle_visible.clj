(ns gdl.actor.toggle-visible
  (:require [gdl.actor.set-visible :refer [set-visible!]]
            [gdl.actor.visible :refer [visible?]]))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))
