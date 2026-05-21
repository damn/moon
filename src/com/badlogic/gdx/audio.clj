(ns com.badlogic.gdx.audio
  (:require [clojure.audio :as audio])
  (:import (com.badlogic.gdx Audio)))

(extend-type Audio
  audio/Audio
  (new-sound [this file-handle]
    (.newSound this file-handle)))
