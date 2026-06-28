(ns editor.window.with-window-close
  (:require [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Window)
           (scene2d Stage)))

(defn- find-window-ancestor [actor]
  (loop [a actor]
    (if-let [p (Actor/.getParent a)]
      (if (instance? Window p)
        p
        (recur p))
      (throw (Error. (str "Actor has no parent window " actor))))))

(defn f [f]
  (fn [actor {:keys [ctx/skin
                     ctx/stage]
              :as ctx}]
    (try
     (let [new-ctx (update ctx :ctx/db f)
           stage (Actor/.getStage actor)]
       (set! (.ctx stage) new-ctx))
     (Actor/.remove (find-window-ancestor actor))
     (catch Throwable t
       (throwable/pretty-pst t)
       (Stage/.addActor stage
                        (error-window/create
                         {:type :ui/error-window
                          :skin skin
                          :throwable t}))))))
