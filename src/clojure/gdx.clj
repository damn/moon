(ns clojure.gdx
  (:require clojure.audio.sound)
  (:import (com.badlogic.gdx Gdx)
           (com.badlogic.gdx.audio Sound)))

(defn app []
  Gdx/app)

(extend-type Sound
  clojure.audio.sound/Sound
  (play! [this]
    (.play this)))
