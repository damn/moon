(ns audio.dispose
  (:import (com.badlogic.gdx.audio Sound)))

(defn dispose! [sounds]
  (run! Sound/.dispose (vals sounds)))
