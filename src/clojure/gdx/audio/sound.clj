(ns clojure.gdx.audio.sound
  (:require [clojure.audio.sound :as sound])
  (:import (com.badlogic.gdx.audio Sound)))

(extend-type Sound
  sound/Sound
  (play! [this]
    (.play this)))
