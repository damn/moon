(ns clojure.gdx.input
  (:import (com.badlogic.gdx Input
                             Input$Buttons
                             Input$Keys)))

(defn- k->value [k]
  (case k
    :input.keys/enter  Input$Keys/ENTER
    :input.keys/minus  Input$Keys/MINUS
    :input.keys/equals Input$Keys/EQUALS
    :input.keys/p      Input$Keys/P
    :input.keys/space  Input$Keys/SPACE
    :input.keys/escape Input$Keys/ESCAPE
    :input.keys/i      Input$Keys/I
    :input.keys/e      Input$Keys/E
    :input.keys/d      Input$Keys/D
    :input.keys/a      Input$Keys/A
    :input.keys/w      Input$Keys/W
    :input.keys/s      Input$Keys/S
    ))

(defn- b->value [b]
  (case b
    :input.buttons/left  Input$Buttons/LEFT
    :input.buttons/right Input$Buttons/RIGHT
    ))

(defn key-pressed? [^Input input key]
  (.isKeyPressed input (k->value key)))

(defn key-just-pressed? [^Input input key]
  (.isKeyJustPressed input (k->value key)))

(defn button-just-pressed? [^Input input button]
  (.isButtonJustPressed input (b->value button)))

(defn mouse-position [^Input input]
  [(.getX input)
   (.getY input)])

(defn set-processor! [^Input input input-processor]
  (.setInputProcessor input input-processor))
