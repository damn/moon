(ns moon.gdx
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [moon.viewport :as viewport])
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn fit-viewport [width height camera]
  (proxy [FitViewport ILookup] [width height camera]
    (valAt [k]
      (case k
        :viewport/camera       (.getCamera      this)
        :viewport/world-width  (.getWorldWidth  this)
        :viewport/world-height (.getWorldHeight this)
        ))))

; 1. ScalingViewport
; 2. scaling field private
; 3. screenWidth / screenHeight private

(extend-type FitViewport
  viewport/Viewport
  ;	/** Applies the viewport to the camera and sets the glViewport.
  ;	 * @param centerCamera If true, the camera position is set to the center of the world. */
  ;	public void apply (boolean centerCamera) {
  ;		HdpiUtils.glViewport(screenX, screenY, screenWidth, screenHeight);
  ;		camera.viewportWidth = worldWidth;
  ;		camera.viewportHeight = worldHeight;
  ;		if (centerCamera) camera.position.set(worldWidth / 2, worldHeight / 2, 0);
  ;		camera.update();
  ;	}

  (update! [viewport screen-width screen-height center-camera?]
    ; ScalingViewport
    (.update viewport screen-width screen-height center-camera?)

    ;	Vector2 scaled = scaling.apply(getWorldWidth(), getWorldHeight(), screenWidth, screenHeight);
    ;	int viewportWidth = Math.round(scaled.x);
    ;	int viewportHeight = Math.round(scaled.y);

    ;	// Center.
    ;	setScreenBounds((screenWidth - viewportWidth) / 2, (screenHeight - viewportHeight) / 2, viewportWidth, viewportHeight);
    ; just calls 'set!'

    ;	apply(centerCamera);
    )

  (unproject [viewport position]

    ; Viewport

    ; Vector2 touchCoords PARAM
		; tmp.set(touchCoords.x, touchCoords.y, 1);
		; camera.unproject(tmp, screenX, screenY, screenWidth, screenHeight);
		; touchCoords.set(tmp.x, tmp.y);
		; return touchCoords;

    (-> viewport
        (.unproject (vector2/->java position))
        vector2/->clj)))

; TODO
; * go deeper
; * viewport is here only 2 functions working on camera probably ?
