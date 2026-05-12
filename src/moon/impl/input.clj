(ns moon.impl.input
  (:require [com.badlogic.gdx.input.keys :as input.keys]
            [clojure.math.vector2 :as v]
            [moon.input :as input])
  (:import (com.badlogic.gdx Gdx
                             Input)))

(defn create [_ctx]
  Gdx/input)

(extend-type Input
  moon.input/Input
  (set-processor! [this input-processor]
    (.setInputProcessor this input-processor))

  (key-pressed? [this key]
    (.isKeyPressed this key))

  (key-just-pressed? [this key]
    (.isKeyJustPressed this key))

  (button-just-pressed? [this button]
    (.isButtonJustPressed this button))

  (mouse-position [this]
    [(.getX this)
     (.getY this)])

  (player-movement-vector [this]
    (let [r (when (input/key-pressed? this input.keys/d) [1  0])
          l (when (input/key-pressed? this input.keys/a) [-1 0])
          u (when (input/key-pressed? this input.keys/w) [0  1])
          d (when (input/key-pressed? this input.keys/s) [0 -1])]
      (when (or r l u d)
        (let [v (v/add-vs (remove nil? [r l u d]))]
          (when (pos? (v/length v))
            v))))))
