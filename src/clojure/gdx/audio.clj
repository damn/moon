(ns clojure.gdx.audio
  (:require clojure.audio)
  (:import (com.badlogic.gdx Audio)))

(extend-type Audio
  clojure.audio/Audio
  (new-sound [this file-handle]
    (.newSound this file-handle)))
