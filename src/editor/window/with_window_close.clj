(ns editor.window.with-window-close
  (:require [clojure.scenes.scene2d.actor.get-stage :refer [get-stage]]
            [clojure.scenes.scene2d.actor.remove :refer [remove!]]
            [clojure.scenes.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [clojure.scenes.scene2d.stage.set-ctx :refer [set-ctx!]]
            [clojure.scenes.scene2d.stage.add-actor :refer [add-actor!]]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window]
            [scene2d.actor.is-window :as window?]))

(defn f [f]
  (fn [actor {:keys [ctx/skin
                     ctx/stage]
              :as ctx}]
    (try
     (let [new-ctx (update ctx :ctx/db f)
           stage (get-stage actor)]
       (set-ctx! stage new-ctx))
     (remove! (find-ancestor actor window?/f))
     (catch Throwable t
       (throwable/pretty-pst t)
       (add-actor! stage
                   (error-window/create
                    {:type :ui/error-window
                     :skin skin
                     :throwable t}))))))
