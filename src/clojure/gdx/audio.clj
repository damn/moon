(ns clojure.gdx.audio
  (:require clojure.audio
            clojure.audio.sound)
  (:import (com.badlogic.gdx Audio)
           (com.badlogic.gdx.audio Sound)))

(extend-type Audio
  clojure.audio/Audio
  (new-sound [this file-handle]
    (.newSound this file-handle)))

(extend-type Sound
  clojure.audio.sound/Sound
  (play! [this]
    (.play this)))
