(ns clojure.gdx.scene2d.actor.toggle-visible
  (:require [clojure.gdx.scene2d.actor :refer [set-visible!]])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn toggle-visible! [^Actor actor]
  (set-visible! actor (not (.isVisible actor))))
