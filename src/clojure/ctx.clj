(ns clojure.ctx
  (:require [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.gl20 :as gl20]))

(defn clear
  [{:keys [ctx/graphics] :as ctx}]
  (let [gl (graphics/getGL20 graphics)]
    (gl20/glClearColor gl 0 0 0 0)
    (gl20/glClear gl gl20/GL_COLOR_BUFFER_BIT))
  ctx)
