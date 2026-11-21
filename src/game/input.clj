(ns game.input
  (:require
   [clojure.com.badlogic.gdx.input :as input]
   [clojure.com.badlogic.gdx.input-buttons :as input-buttons]
   [clojure.com.badlogic.gdx.input-keys :as input-keys]))

(def ^:private key->code
  {:keys/a input-keys/A
   :keys/d input-keys/D
   :keys/down input-keys/DOWN
   :keys/e input-keys/E
   :keys/enter input-keys/ENTER
   :keys/equals input-keys/EQUALS
   :keys/escape input-keys/ESCAPE
   :keys/i input-keys/I
   :keys/left input-keys/LEFT
   :keys/minus input-keys/MINUS
   :keys/p input-keys/P
   :keys/right input-keys/RIGHT
   :keys/s input-keys/S
   :keys/space input-keys/SPACE
   :keys/u input-keys/U
   :keys/up input-keys/UP
   :keys/w input-keys/W})

(def ^:private button->code
  {:buttons/left input-buttons/LEFT})

(defn- key-code [k]
  (or (key->code k)
      (throw (ex-info "Unknown key" {:key k}))))

(defn- button-code [b]
  (or (button->code b)
      (throw (ex-info "Unknown button" {:button b}))))

(defn key-pressed? [input k]
  (input/key-pressed? input (key-code k)))

(defn key-just-pressed? [input k]
  (input/key-just-pressed? input (key-code k)))

(defn button-just-pressed? [input b]
  (input/button-just-pressed? input (button-code b)))

(defn x [input]
  (input/x input))

(defn y [input]
  (input/y input))

(defn set-processor! [input processor]
  (input/set-input-processor! input processor))
