(ns clojure.input
  (:require [com.badlogic.gdx.input :as input]))

(defn get-x [& args]
  (apply input/get-x args))

(defn get-y [& args]
  (apply input/get-y args))

(defn button-just-pressed? [& args]
  (apply input/button-just-pressed? args))

(defn key-just-pressed? [& args]
  (apply input/key-just-pressed? args))

(defn key-pressed? [& args]
  (apply input/key-pressed? args))

(defn set-input-processor! [& args]
  (apply input/set-input-processor! args))
