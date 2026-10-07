(ns editor.ui.property
  (:require [clj-commons.pretty.repl :as pretty-repl]
            [editor.ui.util :refer [find-ancestor]]
            [editor.ui.widget :refer [create-widget map-widget-value widget-value]]
            [moon.db :as db])
  (:import (com.badlogic.gdx Gdx Input Input$Keys)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui ScrollPane Table TextButton Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn- with-window-close [state f]
  (fn [actor]
    (try
     (swap! state update :db f)
     (.remove ^Actor (find-ancestor actor (partial instance? Window)))
     (catch Throwable t
       (binding [*print-level* 3]
         (pretty-repl/pretty-pst t 24))))))

(defn- property-editor-table [state widget on-save on-delete]
  (let [{:keys [skin]} @state
        table (doto (Table.)
                (#(.pad (.defaults ^Table %) (float 5))))]
    (doto (.add table ^Actor widget)
      (.colspan (int 2)))
    (.row table)
    (doto (.add table ^Actor (doto (TextButton. "Save [LIGHT_GRAY](ENTER)[]" skin)
                               (.addListener (proxy [ChangeListener] []
                                               (changed [event actor]
                                                 (on-save actor))))))
      (.center))
    (doto (.add table ^Actor (doto (TextButton. "Delete" skin)
                               (.addListener (proxy [ChangeListener] []
                                               (changed [event actor]
                                                 (on-delete actor))))))
      (.center))
    (.row table)
    (.pack table)
    table))

(declare property-editor-window)

(defn- rebuild-editor-window! [state]
  (let [{:keys [db stage]} @state
        window (-> (.getRoot ^Stage stage)
                   (.findActor "moon.ui.clojure.editor-window"))
        map-widget-table (.findActor ^Group window "moon.db.schema.map.ui.widget")
        property (map-widget-value map-widget-table (:db/schemas db))]
    (.remove window)
    (.addActor ^Stage stage (property-editor-window state property))))

(defn property-editor-window [state property]
  (swap! state assoc :rebuild-editor-window! #(rebuild-editor-window! state))
  (let [{:keys [db skin stage]} @state
        schemas (:db/schemas db)
        schema (get schemas (keyword "properties" (namespace (:property/id property))))
        widget (create-widget state schema property)
        scroll-pane-height (.getWorldHeight (.getViewport ^Stage stage))
        get-widget-value #(widget-value schema widget schemas)
        property-id (:property/id property)
        on-delete (with-window-close state (fn [db]
                                             (db/delete! db property-id)))
        on-save (with-window-close state (fn [db]
                                           (db/update! db (get-widget-value))))
        ^Table table (property-editor-table state widget on-save on-delete)
        window (Window. "[SKY]Property[]" skin)]
    (.pad (.defaults window) (float 5))
    (let [c (.add window (ScrollPane. table skin))]
      (.width c (float (+ (.getWidth table) 50)))
      (.height c (float (min (- scroll-pane-height 50)
                             (.getHeight table)))))
    (.row window)
    (.pack window)
    (.add (.getTitleTable window)
          (doto (TextButton. "X" skin)
            (.addListener (proxy [ChangeListener] []
                            (changed [_event _actor]
                              (.remove window))))))
    (.setModal window true)
    (.addActor window (proxy [Actor] []
                        (act [delta]
                          (when (.isKeyJustPressed Gdx/input Input$Keys/ENTER)
                            (on-save this))
                          (let [^Actor this this]
                            (proxy-super act delta)))
                        (draw [batch parent-alpha])))
    (.setName window "moon.ui.clojure.editor-window")
    window))
