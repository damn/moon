(ns moon.render.clear-screen
  (:require [clojure.gdx.app :as app]
            [clojure.graphics :as graphics])
  (:import (com.badlogic.gdx.graphics GL20)))

(defn step [{:keys [ctx/app] :as ctx}]
  (let [^GL20 gl (graphics/gl20 (app/graphics app))]
    (.glClearColor gl 0 0 0 0)
    (.glClear gl GL20/GL_COLOR_BUFFER_BIT))
  ctx)
