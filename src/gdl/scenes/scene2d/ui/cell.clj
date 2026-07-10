(ns gdl.scenes.scene2d.ui.cell
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]))

(defn bottom! [& args]
  (apply cell/bottom args))

(defn center! [& args]
  (apply cell/center args))

(defn colspan! [& args]
  (apply cell/colspan args))

(defn expand! [& args]
  (apply cell/expand args))

(defn expand-x! [& args]
  (apply cell/expandX args))

(defn expand-y! [& args]
  (apply cell/expandY args))

(defn fill-x! [& args]
  (apply cell/fillX args))

(defn fill-y! [& args]
  (apply cell/fillY args))

(defn height! [& args]
  (apply cell/height args))

(defn left! [& args]
  (apply cell/left args))

(defn pad! [& args]
  (apply cell/pad args))

(defn pad-bottom! [& args]
  (apply cell/padBottom args))

(defn pad-top! [& args]
  (apply cell/padTop args))

(defn right! [& args]
  (apply cell/right args))

(defn width! [& args]
  (apply cell/width args))
