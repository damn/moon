(ns clojure.gdx.scene2d.actor.toggle-visible
  (:require [clojure.gdx.scene2d.actor.set-visible :refer [set-visible!]]
            [clojure.gdx.scene2d.actor :refer [visible?]]))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))
