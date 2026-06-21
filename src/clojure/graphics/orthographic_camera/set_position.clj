(ns clojure.graphics.orthographic-camera.set-position
  (:require [clojure.graphics.orthographic-camera.update :refer [update!]])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn set-position! [^OrthographicCamera camera [x y]]
  (set! (.x (.position camera)) x)
  (set! (.y (.position camera)) y)
  (update! camera))
