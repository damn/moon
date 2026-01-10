(ns moon.audio
  (:require [moon.disposable :as disposable])
  (:import (com.badlogic.gdx.audio Sound)))

(defn sound-names [sounds]
  (map first sounds))

(defn play! [sounds sound-name]
  (assert (contains? sounds sound-name) (str sound-name))
  (Sound/.play (get sounds sound-name)))

(defn dispose! [sounds]
  (run! disposable/dispose! (vals sounds)))
