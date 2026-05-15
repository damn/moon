(ns com.badlogic.gdx.application
  (:import (com.badlogic.gdx Application)
           (com.badlogic.gdx.graphics GL20)))

(defn new-sound [^Application app path]
  (.newSound (.getAudio app)
             (.internal (.getFiles app) path)))

(defn frames-per-second [^Application app]
  (.getFramesPerSecond (.getGraphics app)))

(defn delta-time [^Application app]
  (.getDeltaTime (.getGraphics app)))

(defn new-cursor [^Application app pixmap hotspot-x hotspot-y]
  (.newCursor (.getGraphics app) pixmap hotspot-x hotspot-y))

(defn set-cursor! [^Application app cursor]
  (.setCursor (.getGraphics app) cursor))

(defn clear! [^Application app r g b a]
  (.glClearColor (.getGL20 (.getGraphics app)) r g b a)
  (.glClear      (.getGL20 (.getGraphics app)) GL20/GL_COLOR_BUFFER_BIT))

(defn set-input-processor! [^Application this input-processor]
  (.setInputProcessor (.getInput this) input-processor))

(defn key-pressed? [^Application this key]
  (.isKeyPressed (.getInput this) key))

(defn key-just-pressed? [^Application this key]
  (.isKeyJustPressed (.getInput this) key))

(defn button-just-pressed? [^Application this button]
  (.isButtonJustPressed (.getInput this) button))

(defn mouse-position [^Application this]
  [(.getX (.getInput this))
   (.getY (.getInput this))])
