(ns com.badlogic.gdx.graphics.orthographic-camera.set-zoom
  (:require [com.badlogic.gdx.graphics.orthographic-camera.update :refer [update!]])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn set-zoom! [^OrthographicCamera camera amount]
  (set! (.zoom camera) amount)
  (update! camera))
