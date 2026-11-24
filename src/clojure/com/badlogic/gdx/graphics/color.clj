(ns clojure.com.badlogic.gdx.graphics.color
  "A color class, holding the r, g, b and alpha component as floats in the range [0,1]. All methods perform clamping on the
  internal values after execution."
  (:import (com.badlogic.gdx.graphics Color)))

(def white Color/WHITE)

(defn create
  "Constructor, sets the components of the color
  
  - `r`: the red component
  
  - `g`: the green component
  
  - `b`: the blue component
  
  - `a`: the alpha component"
  [r g b a]
  (Color. (float r) (float g) (float b) (float a)))

(defn float-bits
  "Packs the color components into a 32-bit integer with the format ABGR and then converts it to a float. Alpha is compressed
  from 0-255 to use only even numbers between 0-254 to avoid using float bits in the NaN range (see
  int to float color). Converting a color to a float and back can be lossy for alpha.
  
  Returns the packed color as a 32-bit float"
  ([^Color color]
   (.toFloatBits color))
  ([r g b a]
   (Color/toFloatBits (float r) (float g) (float b) (float a))))
