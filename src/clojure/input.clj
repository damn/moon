(ns clojure.input
  (:require [com.badlogic.gdx.input :as input]))

(defn get-x [& args]
  (apply input/getX args))

(defn get-y [& args]
  (apply input/getY args))

(defn button-just-pressed? [& args]
  (apply input/isButtonJustPressed args))

(defn key-just-pressed? [& args]
  (apply input/isKeyJustPressed args))

(defn key-pressed? [& args]
  (apply input/isKeyPressed args))

(defn set-input-processor! [& args]
  (apply input/setInputProcessor args))
