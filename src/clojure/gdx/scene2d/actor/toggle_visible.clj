(ns clojure.gdx.scene2d.actor.toggle-visible
  (:require [clojure.gdx.scene2d.actor :refer [set-visible!
                                               visible?]]))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))
