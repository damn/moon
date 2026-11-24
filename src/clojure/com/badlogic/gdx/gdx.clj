(ns clojure.com.badlogic.gdx.gdx
  "Environment class holding references to the application, [[clojure.com.badlogic.gdx.graphics]], [[clojure.com.badlogic.gdx.audio]], [[clojure.com.badlogic.gdx.files]] and
  [[clojure.com.badlogic.gdx.input]] instances. The references are held in public static fields which allows static access to all sub systems. Do not
  use [[clojure.com.badlogic.gdx.graphics]] in a thread that is not the rendering thread.
  
  This is normally a design faux pas but in this case is better than the alternatives."
  (:import (com.badlogic.gdx Gdx)))

(defn audio []
  Gdx/audio)

(defn files []
  Gdx/files)

(defn input []
  Gdx/input)

(defn graphics []
  Gdx/graphics)
