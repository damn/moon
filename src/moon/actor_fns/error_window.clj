(ns moon.actor-fns.error-window
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]
            [clojure.repl :as repl]
            [moon.table :as table]
            [moon.window :as window]))

(defmacro ^:private with-err-str [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defn create
  [{:keys [skin throwable]}]
  (let [label-text (binding [*print-level* 3]
                     (with-err-str
                       (repl/pst throwable)))]
    (doto (gdx-window/create "Error" skin)
      (window/add-close-button! skin)
      (table/add-rows! [[{:actor (label/create label-text skin)}]])
      (gdx-window/set-modal! true)
      (widget-group/pack!))))
