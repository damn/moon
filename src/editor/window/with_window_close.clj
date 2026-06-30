(ns editor.window.with-window-close
  (:require [clojure.gdx :as gdx]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window]
            [scene2d.actor.find-ancestor :refer [find-ancestor]]))

(defn f [f]
  (fn [actor {:keys [ctx/skin
                     ctx/stage]
              :as ctx}]
    (try
     (let [new-ctx (update ctx :ctx/db f)
           stage (gdx/get-stage actor)]
       (set! (.ctx stage) new-ctx))
     (gdx/remove! (find-ancestor actor gdx/window?))
     (catch Throwable t
       (throwable/pretty-pst t)
       (gdx/add-actor! stage
                       (error-window/create
                        {:type :ui/error-window
                         :skin skin
                         :throwable t}))))))
