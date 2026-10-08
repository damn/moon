(ns audio.create
  (:import (com.badlogic.gdx Audio)))

(defn create-audio! [^Audio audio file-handles]
  (update-vals file-handles #(.newSound audio %)))
