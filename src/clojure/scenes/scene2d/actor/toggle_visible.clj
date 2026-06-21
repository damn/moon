(ns clojure.scenes.scene2d.actor.toggle-visible
  (:require [clojure.scenes.scene2d.actor.set-visible :refer [set-visible!]]
            [clojure.scenes.scene2d.actor.visible :refer [visible?]]))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))
